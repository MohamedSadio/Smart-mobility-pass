package com.projet.smartmobility.bilingservice.repository;

import com.projet.smartmobility.bilingservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /** Historique des transactions d'un pass, triées par date décroissante. */
    List<Transaction> findByPassNumberOrderByCreatedAtDesc(String passNumber);

    /** Historique des transactions d'un utilisateur, triées par date décroissante. */
    List<Transaction> findByUserIdOrderByCreatedAtDesc(UUID userId);
}