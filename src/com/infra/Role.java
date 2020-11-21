package com.infra;

import java.util.List;

public class Role {
    private String name;
    private List<Permissions> permissionActionList;
    private List<Resource> permissionResourceList;

    public Role(String name, List<Permissions> permissionActionList, List<Resource> permissionResourceList) {
        this.name = name;
        this.permissionActionList = permissionActionList;
        this.permissionResourceList = permissionResourceList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permissions> getPermissionActionList() {
        return permissionActionList;
    }

    public void setPermissionActionList(List<Permissions> permissionActionList) {
        this.permissionActionList = permissionActionList;
    }

    public List<Resource> getPermissionResourceList() {
        return permissionResourceList;
    }

    public void setPermissionResourceList(List<Resource> permissionResourceList) {
        this.permissionResourceList = permissionResourceList;
    }
}
