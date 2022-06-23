package org.paperplane.conference.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.paperplane.conference.api.response.UserResponse;
import org.paperplane.conference.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(resultType = UserResponse.class)
    UserResponse toUserResponse(User user);
}
