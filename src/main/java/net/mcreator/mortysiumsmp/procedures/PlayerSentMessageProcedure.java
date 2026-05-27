package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber
public class PlayerSentMessageProcedure {
    
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1476387466347806740/GeV5MoPvGdN8yAAo6br5zVx5HUN7asY1j5lsfVab3UmNKB8UVPBM5NqpySwmdlS67VLc";
    private static final String SKIN_URL_TEMPLATE = "https://mc-heads.net/avatar/%s/64";
    
    private static final Set<String> ALLOWED_SERVERS = new HashSet<>();
    static {
        ALLOWED_SERVERS.add("sp-03.redhosting.com.br");
        ALLOWED_SERVERS.add("sp-03.redhosting.com.br:25593");
        ALLOWED_SERVERS.add("0.0.0.0");
        ALLOWED_SERVERS.add("127.0.0.1");
        ALLOWED_SERVERS.add("localhost");
    }
    

    private static final long CACHE_DURATION_MS = 2000L;
    private static final int MAX_CACHE_SIZE = 100;
    private static final Map<String, Long> messageCache = new ConcurrentHashMap<>();
    
    private static final Pattern COLOR_CODES = Pattern.compile("[&§][0-9a-fk-or]");
    private static final Pattern CHAT_PREFIXES = Pattern.compile("^(?:<[^>]+>|\\[[^\\]]+\\]|[^:»]+[»:]|\\*[^*]+\\*)\\s*");
    private static final Pattern EXTRA_SPACES = Pattern.compile("\\s+");
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChat(ServerChatEvent event) {
        execute(event);
    }

    public static void execute() {
        execute(null);
    }

    private static void execute(ServerChatEvent event) {
        if (event == null) return;
        if (!isAllowedServer()) return;
        if (event.getPlayer() == null) return;
        
        try {
            String playerName = event.getPlayer().getName().getString();
            String message = extractMessage(event);
            
            if (isValidMessage(message)) {
                String cleanMessage = cleanMessage(message, playerName);
                
                if (isValidMessage(cleanMessage) && !isDuplicate(playerName, cleanMessage)) {
                    sendToDiscord(playerName, cleanMessage);
                    cleanupCache();
                }
            }
        } catch (Exception e) {

        }
    }
    
    private static String extractMessage(ServerChatEvent event) {
        String message = null;
        
        try {
            message = event.getRawText();
            if (isValidMessage(message)) return message;
        } catch (Exception ignored) {}
        
        try {
            Component component = event.getMessage();
            if (component != null) {
                message = component.getString();
                if (isValidMessage(message)) return message;
            }
        } catch (Exception ignored) {}
        
        try {
            Component component = event.getMessage();
            if (component != null && component.getContents() != null) {
                message = component.getContents().toString();
                if (isValidMessage(message)) return message;
            }
        } catch (Exception ignored) {}
        
        return null;
    }
    
    private static String cleanMessage(String message, String playerName) {
        if (!isValidMessage(message)) return null;
        
        String cleaned = message;
        
        cleaned = COLOR_CODES.matcher(cleaned).replaceAll("");
        
        cleaned = CHAT_PREFIXES.matcher(cleaned).replaceFirst("");
        
        cleaned = EXTRA_SPACES.matcher(cleaned).replaceAll(" ").trim();
        
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(playerName)) {
            return null;
        }
        
        return cleaned;
    }
    
    private static boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty();
    }
    
    private static boolean isDuplicate(String playerName, String message) {
        String cacheKey = playerName + ":" + message.hashCode();
        long now = System.currentTimeMillis();
        
        Long lastSent = messageCache.get(cacheKey);
        if (lastSent != null && (now - lastSent) < CACHE_DURATION_MS) {
            return true; // É duplicada
        }
        
        messageCache.put(cacheKey, now);
        return false;
    }
    
    private static boolean isAllowedServer() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return false;
            
            String host = server.getLocalIp();
            if (host == null) return false;
            
            int port = server.getPort();
            
            System.out.println("[Discord Webhook] Host detectado: " + host);
            System.out.println("[Discord Webhook] Porta detectada: " + port);
            
            boolean isAllowed = ALLOWED_SERVERS.contains(host) ||
                               ALLOWED_SERVERS.contains(host + ":" + port) ||
                               "0.0.0.0".equals(host) ||
                               host.isEmpty();
            
            if (isAllowed) {
                System.out.println("[Discord Webhook] Servidor autorizado confirmado!");
            } else {
                System.out.println("[Discord Webhook] BLOQUEADO - Servidor não autorizado: " + host + ":" + port);
            }
            
            return isAllowed;
                    
        } catch (Exception e) {
            System.err.println("[Discord Webhook] Erro ao verificar servidor: " + e.getMessage());
            return false;
        }
    }
    
    private static void cleanupCache() {
        if (messageCache.size() <= MAX_CACHE_SIZE) return;
        
        long threshold = System.currentTimeMillis() - (CACHE_DURATION_MS * 10);
        messageCache.entrySet().removeIf(entry -> entry.getValue() < threshold);
    }
    
    private static void sendToDiscord(String playerName, String message) {
        CompletableFuture.runAsync(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(WEBHOOK_URL);
                conn = (HttpURLConnection) url.openConnection();
                
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("User-Agent", "Minecraft-Discord-Bridge/2.0");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                
                String avatarUrl = String.format(SKIN_URL_TEMPLATE, playerName);
                String json = buildJsonPayload(playerName, avatarUrl, message);
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    os.flush();
                }
                
                int status = conn.getResponseCode();
                if (status == 200 || status == 204) {
                    System.out.println("[Discord Webhook] ✓ Mensagem enviada: " + playerName);
                } else {
                    System.err.println("[Discord Webhook] ✗ Erro HTTP " + status);
                    logErrorResponse(conn);
                }
                
            } catch (Exception e) {
                System.err.println("[Discord Webhook] Erro ao enviar: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }
    
    private static String buildJsonPayload(String username, String avatarUrl, String content) {
        return String.format(
            "{\"username\":\"%s\",\"avatar_url\":\"%s\",\"content\":\"%s\"}",
            escapeJson(username),
            avatarUrl,
            escapeJson(content)
        );
    }
    
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f");
    }
    
    private static void logErrorResponse(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.err.println("[Discord Webhook] Resposta de erro: " + response.toString());
        } catch (Exception e) {
            System.err.println("[Discord Webhook] Não foi possível ler a resposta de erro");
        }
    }
    
    public static void testWebhook() {
        sendToDiscord("Sistema", "✅ Webhook funcionando corretamente!");
    }
    
    public static void testMessage(String playerName, String message) {
        sendToDiscord(playerName, message);
    }
}