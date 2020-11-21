package com.infra;

import java.util.Objects;

public class Resource {
    private String name;
    private Status status;

    public Resource(String name) {
        this.name = name;
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

    public boolean isAvailable(){
        return Status.AVAILABLE.equals(this.status);
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
