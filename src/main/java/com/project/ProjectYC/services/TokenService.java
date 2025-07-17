package com.project.ProjectYC.services;

import com.project.ProjectYC.models.AuthToken;
import com.project.ProjectYC.repository.AuthTokenRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TokenService {
    @Autowired
    AuthTokenRepo authRepo;
    private static final Logger LG = LogManager.getLogger() ; // Add a logger for logging purposes using Log4j

    //Enregistrer Token
    public void ajouterToken(AuthToken authToken) {
        authRepo.save(authToken);
    }

    //Chercher Token
    public Boolean chercherToken(String token) {
        try {
            return authRepo.findById(token).get().getValue() != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean supprimerToken(String token){
        try {
            authRepo.deleteById(token);
            return true ;
        }catch (NoSuchElementException e){
            LG.error("[!] Failed to delete token , token not found");
            return false ;
        }
    }
}
