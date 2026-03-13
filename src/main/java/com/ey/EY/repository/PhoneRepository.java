package com.ey.EY.repository;

import com.ey.EY.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PhoneRepository extends JpaRepository<Phone, UUID> {}
