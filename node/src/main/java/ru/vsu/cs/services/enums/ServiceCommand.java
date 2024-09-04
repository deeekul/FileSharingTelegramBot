package ru.vsu.cs.services.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String v) {
        for (ServiceCommand cmd: ServiceCommand.values()) {
            if (cmd.value.equals(v)) {
                return cmd;
            }
        }
        return null;
    }
}
