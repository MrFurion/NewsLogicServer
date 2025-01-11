package by.clevertec.enums;

public enum Roles {
    ADMIN("ROLE_ADMIN"),
    VIEWER("ROLE_VIEWER"),
    JOURNALIST("ROLE_JOURNALIST"),
    SUBSCRIBER("ROLE_SUBSCRIBER");
    private final String roleName;
    Roles(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleName(){
        return roleName;
    }
}
