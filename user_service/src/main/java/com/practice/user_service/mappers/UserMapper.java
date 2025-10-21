package com.practice.user_service.mappers;

import com.practice.user_service.dto.UserInfoDto;
import com.practice.user_service.entity.UserProfile;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
// 🔍 Revision Note: MapStruct Overview

// MapStruct is a ==COMPILE-TIME== code generator for object mapping (DTO ↔ Entity).

// ✅ Generates type-safe, fast, reflection-free mapping code.
// ✅ Widely used in enterprise/MNC-level Spring Boot applications like SAP, Red Hat, Deutsche Telekom, Ericsson, etc.
// ✅ No runtime overhead — mappings are precompiled into plain Java.
// ✅ Improves maintainability by removing boilerplate mapping code.

// 🔧 Configurable via @Mapper (e.g., componentModel="spring") and strategies like
//    NullValuePropertyMappingStrategy.IGNORE, ReportingPolicy.ERROR.

// 🛡️ Open-source (Apache 2.0) and production-safe — trusted in large-scale systems.
// 💡 Tip: Keep mapping logic centralized and write unit tests for generated mappers.
public interface UserMapper {
    void updateUserProfileFromDto(UserInfoDto dto, @MappingTarget UserProfile entity);
}