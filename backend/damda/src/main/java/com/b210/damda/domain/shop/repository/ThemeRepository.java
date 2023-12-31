package com.b210.damda.domain.shop.repository;

import com.b210.damda.domain.entity.theme.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByThemeNo(Long themeNo);


}
