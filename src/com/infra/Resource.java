package com.infra;

import java.util.Objects;

public class Resource {
    private String name;
    private Status status;
    private Boolean shared;

    public Resource(String name, Boolean shared) {
        this.name = name;
        this.shared = shared;
        this.status = Status.AVAILABLE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // TODO: Implementar que un recurso peuda usarse por mas de uno a la vez.
    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public boolean isAvailable(){
        return Status.AVAILABLE.equals(this.status) || this.shared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(name, resource.name) &&
                status == resource.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status);
    }
}
