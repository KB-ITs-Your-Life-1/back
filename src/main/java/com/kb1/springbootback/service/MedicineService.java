package com.kb1.springbootback.service;

import java.util.List;

import com.kb1.springbootback.model.medicine.Medicine;
import com.kb1.springbootback.model.user.User;
import com.kb1.springbootback.payload.response.MessageResponse;
import com.kb1.springbootback.repository.medicine.MedicineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    // name으로 Medicine 정보 가져오기
    public Medicine getMedicineByName(String name) {
        return medicineRepository.findAllByName(name);
    }

    // name로 Medicine 있는지 확인
    public Boolean existsByName(String name) {
        return medicineRepository.existsByName(name);
    }

    // medicine LIST 다 가져오기
    public List<Medicine> findAll(){
        List<Medicine> list = medicineRepository.findAll();
        System.out.println("medicine LIST 다 가져오기");
        System.out.println("약 이름 ? "+list.get(0).getName());
        return list;
    }

    // 하나씩 다 가져오기
    // public Medicine getMedicine(String name){
    //     Medicine medicine = medicineRepository.findBy(name).orElseThrow(() -> new ResourceNotFoundException("Not exist Medicine Data by name : ["+name+"]"));
        
    //     return medicineRepository.save(medicine);
    // }

}
