package bg.emiliyan.acc_backend.mappers;

import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.dtos.UpdateUserDTO;
import bg.emiliyan.acc_backend.dtos.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RegisterUserDTO -> User
    @Mapping(target = "id", ignore = true) // ID-то ще се генерира от DB
    @Mapping(target = "password", source = "password") // ще encode-ваме после
    User registerUserDTOtoUser(RegisterUserDTO dto);

    // User -> UserDTO
    UserDTO userToUserDTO(User user);

    // UpdateUserDTO -> User (merge)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDTO(UpdateUserDTO dto, @MappingTarget User user);
}
