package com.summer.shortlink.admin.dto.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSaveReqDTO {
    private String name;

    private String username;
}
