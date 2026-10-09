package com.nexusgesti.identity.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class RolePermission {
    private UUID id;
    private String roleCode;
    private Permission permission;
    private Instant grantedAt;
    private UUID grantedBy;
    private LocalDate expireLe;

    public boolean estExpiree(LocalDate date) {
        return expireLe != null && date != null && date.isAfter(expireLe);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }
    public UUID getGrantedBy() { return grantedBy; }
    public void setGrantedBy(UUID grantedBy) { this.grantedBy = grantedBy; }
    public LocalDate getExpireLe() { return expireLe; }
    public void setExpireLe(LocalDate expireLe) { this.expireLe = expireLe; }
}
