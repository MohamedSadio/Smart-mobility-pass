package com.projet.smartmobility.bilingservice.service;

import com.projet.smartmobility.bilingservice.client.MobilityPassClient;
import com.projet.smartmobility.bilingservice.config.RabbitMQConfig;
import com.projet.smartmobility.bilingservice.dto.BillingDto;
import com.projet.smartmobility.bilingservice.entity.Transaction;
import com.projet.smartmobility.bilingservice.entity.TransactionType;
import com.projet.smartmobility.bilingservice.mapper.TransactionMapper;
import com.projet.smartmobility.bilingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final TransactionRepository transactionRepository;
    private final MobilityPassClient    mobilityPassClient;
    private final TransactionMapper     transactionMapper;
    private final RabbitTemplate        rabbitTemplate;

    @Transactional
    public BillingDto.TransactionResponse debit(BillingDto.DebitRequest request) {
        log.info("[BILLING] Débit — pass={}, montant={}", request.passNumber(), request.amount());
        var updatedPass = mobilityPassClient.debit(request.passNumber(), request.amount());
        Transaction tx = transactionRepository.save(
                transactionMapper.toEntity(updatedPass, request.amount(), TransactionType.DEBIT));
        //publishEvent(tx);
        return transactionMapper.toDto(tx);
    }

    @Transactional
    public BillingDto.TransactionResponse recharge(BillingDto.RechargeRequest request) {
        log.info("[BILLING] Rechargement — pass={}, montant={}", request.passNumber(), request.amount());
        var updatedPass = mobilityPassClient.recharge(request.passNumber(), request.amount());
        Transaction tx = transactionRepository.save(
                transactionMapper.toEntity(updatedPass, request.amount(), TransactionType.RECHARGE));
        //publishEvent(tx);
        return transactionMapper.toDto(tx);
    }

    public BillingDto.BalanceResponse getBalance(String passNumber) {
        return transactionMapper.toBalanceResponse(mobilityPassClient.getByPassNumber(passNumber));
    }

    public BillingDto.BalanceResponse getBalanceByUserId(UUID userId) {
        return transactionMapper.toBalanceResponse(mobilityPassClient.getByUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<BillingDto.TransactionResponse> getHistoryByPassNumber(String passNumber) {
        return transactionRepository.findByPassNumberOrderByCreatedAtDesc(passNumber)
                .stream().map(transactionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BillingDto.TransactionResponse> getHistoryByUserId(UUID userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(transactionMapper::toDto).toList();
    }

    // -------------------------------------------------------------------------

//    private void publishEvent(Transaction tx) {
//        try {
//            rabbitTemplate.convertAndSend(
//                    RabbitMQConfig.EXCHANGE_NAME,
//                    RabbitMQConfig.ROUTING_KEY_TX,
//                    transactionMapper.toEvent(tx));
//            log.info("[BILLING] TransactionEvent publié — txId={}", tx.getId());
//        } catch (Exception e) {
//            log.warn("[BILLING] Échec publication RabbitMQ — txId={} : {}", tx.getId(), e.getMessage());
//        }
//    }
}