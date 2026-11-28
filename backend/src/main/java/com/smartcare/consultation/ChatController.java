package com.smartcare.consultation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Chat de la consultation en ligne (polling toutes les 3 secondes cote frontend).
 * GET  /api/consultations/{appointmentId}/messages?after=0
 * POST /api/consultations/{appointmentId}/messages   body: { "contenu": "..." }
 *
 * Acces : seulement le patient ou le medecin du rendez-vous,
 * et seulement si le rendez-vous est EN_LIGNE et CONFIRME.
 */
@RestController
@RequestMapping("/api/consultations/{appointmentId}/messages")
public class ChatController {

    private final JdbcTemplate jdbc;

    public ChatController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record MessageRequest(String contenu) {}

    /** Retourne PATIENT ou MEDECIN si l'utilisateur a le droit, sinon null. */
    private String roleIfAllowed(Long appointmentId, String email) {
        List<String> roles = jdbc.queryForList("""
                SELECT CASE WHEN up.email = ? THEN 'PATIENT' ELSE 'MEDECIN' END
                FROM rendez_vous r
                JOIN patient pa  ON pa.id = r.patient_id
                JOIN app_user up ON up.id = pa.user_id
                JOIN medecin m   ON m.id = r.medecin_id
                JOIN app_user um ON um.id = m.user_id
                WHERE r.id = ? AND r.type = 'EN_LIGNE' AND r.statut = 'CONFIRME'
                  AND (up.email = ? OR um.email = ?)
                """, String.class, email, appointmentId, email, email);
        return roles.isEmpty() ? null : roles.get(0);
    }

    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long appointmentId,
                                  @RequestParam(defaultValue = "0") long after,
                                  Authentication auth) {
        if (roleIfAllowed(appointmentId, auth.getName()) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Acces refuse"));
        }
        List<Map<String, Object>> messages = jdbc.queryForList("""
                SELECT id, sender_role, contenu, created_at
                FROM chat_message
                WHERE rendez_vous_id = ? AND id > ?
                ORDER BY id
                """, appointmentId, after);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<?> send(@PathVariable Long appointmentId,
                                  @RequestBody MessageRequest body,
                                  Authentication auth) {
        String role = roleIfAllowed(appointmentId, auth.getName());
        if (role == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Acces refuse"));
        }
        if (body == null || body.contenu() == null || body.contenu().isBlank()
                || body.contenu().length() > 1000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message vide ou trop long (max 1000 caracteres)"));
        }
        Map<String, Object> saved = jdbc.queryForMap("""
                INSERT INTO chat_message (rendez_vous_id, sender_role, contenu)
                VALUES (?, ?, ?)
                RETURNING id, sender_role, contenu, created_at
                """, appointmentId, role, body.contenu().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
