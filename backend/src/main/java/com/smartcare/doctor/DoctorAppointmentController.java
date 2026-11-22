package com.smartcare.doctor; // adapte le package a celui du projet

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Endpoint de DEMO : le medecin confirme / annule / termine un rendez-vous.
 * PATCH /api/medecins/me/appointments/{id}/status   body: { "status": "CONFIRME" }
 *
 * Utilise JdbcTemplate pour ne pas dependre des entites JPA de Dev 2.
 */
@RestController
@RequestMapping("/api/medecins/me/appointments")
public class DoctorAppointmentController {

    private static final Set<String> ALLOWED = Set.of("CONFIRME", "ANNULE", "TERMINE");

    // transitions autorisees : statut actuel -> statuts possibles
    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            "EN_ATTENTE", Set.of("CONFIRME", "ANNULE"),
            "CONFIRME", Set.of("TERMINE", "ANNULE")
    );

    private final JdbcTemplate jdbc;

    public DoctorAppointmentController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record StatusRequest(String status) {}

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody StatusRequest body,
                                          Authentication auth) {
        // 1. statut demande valide ?
        if (body == null || body.status() == null || !ALLOWED.contains(body.status())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "status doit etre CONFIRME, ANNULE ou TERMINE"));
        }

        // 2. le rendez-vous existe et appartient a CE medecin ? (auth.getName() = email)
        List<String> current = jdbc.queryForList("""
                SELECT r.statut
                FROM rendez_vous r
                JOIN medecin m   ON m.id = r.medecin_id
                JOIN app_user u  ON u.id = m.user_id
                WHERE r.id = ? AND u.email = ?
                """, String.class, id, auth.getName());

        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rendez-vous introuvable"));
        }

        // 3. transition permise ?
        String from = current.get(0);
        if (!TRANSITIONS.getOrDefault(from, Set.of()).contains(body.status())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Transition impossible : " + from + " -> " + body.status()));
        }

        // 4. mise a jour
        jdbc.update("UPDATE rendez_vous SET statut = ? WHERE id = ?", body.status(), id);
        return ResponseEntity.ok(Map.of("id", id, "status", body.status()));
    }
}
