package com.hackathon.back.service;

import com.hackathon.back.model.Portal;
import com.hackathon.back.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

@Service
public class PortalService {
    
    @Autowired
    private PortalRepository portalRepository;
    
    public String generatePortalHash(String domain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(domain.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }

    public Portal createPortal(String name, String url, String email) {
        if (portalRepository.existsByUrl(url)) {
            throw new RuntimeException("Portal já registrado");
        }

        Portal portal = new Portal();
        portal.setName(name);
        portal.setUrl(url);
        portal.setEmail(email);
        portal.setVerified(false);
        
        // Gera o hash do domínio
        String domain = extractDomain(url);
        String hash = generatePortalHash(domain);
        portal.setSecretHash(hash);
        
        return portalRepository.save(portal);
    }

    public boolean verifyPortal(String url) {
        try {
            String domain = extractDomain(url);
            String expectedHash = generatePortalHash(domain);
            
            // Busca os registros TXT do domínio
            Record[] records = new Lookup(domain, Type.TXT).run();
            if (records == null) {
                return false;
            }

            // Verifica se algum dos registros TXT contém o hash esperado
            for (Record record : records) {
                if (record instanceof TXTRecord) {
                    TXTRecord txtRecord = (TXTRecord) record;
                    for (String txt : txtRecord.getStrings()) {
                        if (txt.startsWith("verify=")) {
                            String hash = txt.substring(7); // Remove "verify="
                            if (hash.equals(expectedHash)) {
                                // Atualiza o status de verificação no banco
                                Portal portal = portalRepository.findByUrl(url);
                                if (portal != null) {
                                    portal.setVerified(true);
                                    portalRepository.save(portal);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractDomain(String url) {
        // Remove protocolo (http://, https://)
        String domain = url.replaceAll("^(https?://)?(www\\.)?", "");
        // Remove caminho após o domínio
        domain = domain.split("/")[0];
        // Remove porta se existir
        domain = domain.split(":")[0];
        return domain;
    }

    public List<String> getDnsInstructions(String url) {
        String domain = extractDomain(url);
        String hash = generatePortalHash(domain);
        
        List<String> instructions = new ArrayList<>();
        instructions.add("Para verificar seu portal, adicione o seguinte registro TXT ao seu DNS:");
        instructions.add("Nome do registro: " + domain);
        instructions.add("Tipo: TXT");
        instructions.add("Valor: verify=" + hash);
        instructions.add("TTL: 3600 (ou padrão)");
        
        return instructions;
    }
} 