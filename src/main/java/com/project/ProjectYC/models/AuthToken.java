package com.project.ProjectYC.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "authTokenList")
public class AuthToken {
    //This is will store the tokens of users that log in in the database
    @Id
    private String value ;
}
