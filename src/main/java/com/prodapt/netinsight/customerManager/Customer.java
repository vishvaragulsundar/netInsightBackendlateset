package com.prodapt.netinsight.customerManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@Node("Customer")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    @NotEmpty
    private String name;


    @Property("description")
    @NotEmpty
    private String description;

    @Property("emailId")
    @NotEmpty
    @Size(min = 5)
    @Email
    private String emailId;

    @Property("contactNo")
    @NotEmpty
    private String contactNo;

    @Property("customerGroupName")
    private String customerGroupName;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +

                ", description='" + description + '\'' +
                ", emailId='" + emailId + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", customerGroupName=" + customerGroupName +
                '}';
    }
}
