package com.conquistadores.gestionclub.modules.poa.event;

import org.springframework.context.ApplicationEvent;

public class PoaUpdatedEvent extends ApplicationEvent {
    private final String idActividad;
    private final String action; // e.g. "UPDATE_DATE", "DELETE", "CREATE"

    public PoaUpdatedEvent(Object source, String idActividad, String action) {
        super(source);
        this.idActividad = idActividad;
        this.action = action;
    }

    public String getIdActividad() {
        return idActividad;
    }

    public String getAction() {
        return action;
    }
}
