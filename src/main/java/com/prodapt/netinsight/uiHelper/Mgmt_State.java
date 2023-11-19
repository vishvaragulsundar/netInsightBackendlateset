package com.prodapt.netinsight.uiHelper;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class Mgmt_State {

    @EmbeddedId
    private MgmtStateId id;

    public Mgmt_State() {
    }

    public Mgmt_State(String name, String type) {
        this.id = new MgmtStateId(name, type);
    }
@Getter
@Setter
    @Embeddable
    public static class MgmtStateId implements Serializable {

        @Column(name = "NAME")
        private String name;

        @Column(name = "TYPE")
        private String type;

        public MgmtStateId() {
        }

        public MgmtStateId(String name, String type) {
            this.name = name;
            this.type = type;
        }

    }
}
