package com.infra;

import java.util.List;

public class Role {
    private String name;
    private List<Resource> permissionList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Resource> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Resource> permissionList) {
        this.permissionList = permissionList;
    }
}
