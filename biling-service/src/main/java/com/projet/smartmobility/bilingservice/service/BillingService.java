package com.projet.smartmobility.bilingservice.service;

import com.projet.smartmobility.bilingservice.config.RabbitMQConfig;
import com.projet.smartmobility.bilingservice.dto.BillingDto;
import com.projet.smartmobility.bilingservice.entity.Account;
import com.projet.smartmobility.bilingservice.entity.Transaction;
import com.projet.smartmobility.bilingservice.entity.TransactionType;
import com.projet.smartmobility.bilingservice.event.TransactionEvent;
import com.projet.smartmobility.bilingservice.exception.InsufficientFundsException;
import com.projet.smartmobility.bilingservice.repository.AccountRepository;
import com.projet.smartmobility.bilingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService<rabbitTemplate> {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public BillingDto.TransactionResponse debit(BillingDto.DebitRequest req) {
        log.info("Processing debit for userId={} amount={}", req.userId(), req.amount());

        Account account = accountRepository.findByUserId(req.userId())
                .orElseGet(() -> createAccountForUser(req.userId()));

        if (account.getBalance().compareTo(req.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(req.amount()));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccountId(account.getId());
        tx.setUserId(req.userId());
        tx.setType(TransactionType.DEBIT);
        tx.setAmount(req.amount());
        transactionRepository.save(tx);

        publishEvent(tx);

        return toTransactionResponse(tx);
    }

    @Transactional
    public BillingDto.TransactionResponse recharge(BillingDto.RechargeRequest req) {
        log.info("Processing recharge for userId={} amount={}", req.userId(), req.amount());

        Account account = accountRepository.findByUserId(req.userId())
                .orElseGet(() -> createAccountForUser(req.userId()));

        account.setBalance(account.getBalance().add(req.amount()));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccountId(account.getId());
        tx.setUserId(req.userId());
        tx.setType(TransactionType.RECHARGE);
        tx.setAmount(req.amount());
        transactionRepository.save(tx);

        publishEvent(tx);

        return toTransactionResponse(tx);
    }

    @Transactional(readOnly = true)
    public List<BillingDto.TransactionResponse> getHistory(UUID userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    public BillingDto.AccountResponse getBalance(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseGet(() -> createAccountForUser(userId));
        return new BillingDto.AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }

    private Account createAccountForUser(UUID userId) {
        Account account = new Account();
        account.setUserId(userId);
        return accountRepository.save(account);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionCommitted(Transaction tx) {
        publishEvent(tx);
    }

    private void publishEvent(Transaction tx) {
        try {
            TransactionEvent event = new TransactionEvent(
                    tx.getId(),
                    tx.getUserId(),
                    tx.getAccountId(),
                    tx.getAmount(),
                    tx.getType().name(),
                    LocalDateTime.now());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "transaction.completed", event);
            log.info("Published TransactionEvent for txId={}", tx.getId());
        } catch (Exception e) {
            log.warn("Failed to publish TransactionEvent for txId={} (RabbitMQ down?): {}",
                    tx.getId(), e.getMessage());
        }
    }

    private BillingDto.TransactionResponse toTransactionResponse(Transaction t) {
        return new BillingDto.TransactionResponse(
                t.getId(), t.getUserId(), t.getType().name(),
                t.getAmount(), t.getCreatedAt());
    }
}
