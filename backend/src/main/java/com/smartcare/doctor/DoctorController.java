package com.smartcare.doctor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorController {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    public DoctorController(SpecialtyRepository specialtyRepository, DoctorRepository doctorRepository) {
        this.specialtyRepository = specialtyRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/specialties")
    public List<SpecialtyResponse> specialties() {
        return specialtyRepository.findAll().stream()
                .map(s -> new SpecialtyResponse(s.getId(), s.getName()))
                .toList();
    }

    @GetMapping("/doctors")
    @Transactional(readOnly = true)
    public List<DoctorResponse> doctors(@RequestParam(required = false) Long specialtyId) {
        List<Doctor> list = specialtyId == null
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyId(specialtyId);
        return list.stream().map(DoctorResponse::from).toList();
    }
}