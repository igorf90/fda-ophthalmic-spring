package com.fda.ophthalmic.mapper;
import com.fda.ophthalmic.dto.*;
import com.fda.ophthalmic.external.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel="spring")
public interface FdaMapper {
 @Mapping(target="kNumber", source="k_number")
 @Mapping(target="deviceName", source="device_name")
 @Mapping(target="productCode", source="product_code")
 @Mapping(target="decisionDate", source="decision_date")
 @Mapping(target="decisionDescription", source="decision_description")
 @Mapping(target="statementOrSummary", source="statement_or_summary")
 @Mapping(target="clearanceType", source="clearance_type")
 @Mapping(target="advisoryCommittee", source="advisory_committee")
 K510Dto toDto(K510External value);
 @Mapping(target="pmaNumber", source="pma_number")
 @Mapping(target="tradeName", source="trade_name")
 @Mapping(target="productCode", source="product_code")
 @Mapping(target="decisionDate", source="decision_date")
 @Mapping(target="decisionCode", source="decision_code")
 PmaDto toDto(PmaExternal value);
 @Mapping(target="productCode", source="product_code")
 @Mapping(target="deviceName", source="device_name")
 @Mapping(target="regulationNumber", source="regulation_number")
 @Mapping(target="deviceClass", source="device_class")
 @Mapping(target="medicalSpecialtyDescription", source="medical_specialty_description")
 @Mapping(target="reviewPanel", source="review_panel")
 ClassificationDto toDto(ClassificationExternal value);
 @Mapping(target="code", source="term")
 ProductCodeCountDto toDto(CountExternal value);
}
