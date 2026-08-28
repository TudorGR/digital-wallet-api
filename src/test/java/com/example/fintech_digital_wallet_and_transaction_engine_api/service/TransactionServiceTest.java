package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransactionResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransferRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.*;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.InsufficientFundsException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.ResourceNotFoundException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.UnauthorizedAccessException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.mapper.DtoMapper;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.TransactionRepository;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Successful transfer updates sender and receiver balances")
    void transferFunds_success(){

        //Arrange

        when(authentication.getName()).thenReturn("sender@example.com");

        User senderUser=User.builder().id(1L).email("sender@example.com").role(Role.USER).build();
        User receiverUser=User.builder().id(2L).email("receiver@example.com").role(Role.USER).build();

        Wallet senderWallet=Wallet.builder().id(1L).balance(new BigDecimal("100.00")).currency("USD").user(senderUser).build();
        Wallet receiverWallet=Wallet.builder().id(2L).balance(new BigDecimal("50.00")).currency("USD").user(receiverUser).build();

        when(walletRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByIdWithLock(2L))
                .thenReturn(Optional.of(receiverWallet));

        TransferRequest request=new TransferRequest();
        request.setSenderWalletId(1L);
        request.setReceiverWalletId(2L);
        request.setAmount(new BigDecimal("25.00"));

        Transaction savedTransaction=Transaction.builder().id(1L).senderWallet(senderWallet).receiverWallet(receiverWallet).amount(new BigDecimal("25.00")).type(TransactionType.TRANSFER).status(TransactionStatus.SUCCESS).build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse transactionResponse=new TransactionResponse();

        when(dtoMapper.toTransactionResponse(savedTransaction)).thenReturn(transactionResponse);

        //Act

        TransactionResponse result=transactionService.transferFunds(request);

        //Assert

        assertNotNull(result);

        assertEquals(new BigDecimal("75.00"),senderWallet.getBalance());
        assertEquals(new BigDecimal("75.00"),receiverWallet.getBalance());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Transfer fails when sender has insufficient funds")
    void transferFunds_insufficientFunds() {
        //Arrange

        when(authentication.getName()).thenReturn("sender@example.com");

        User senderUser=User.builder().id(1L).email("sender@example.com").role(Role.USER).build();
        User receiverUser=User.builder().id(2L).email("receiver@example.com").role(Role.USER).build();

        Wallet senderWallet=Wallet.builder().id(1L).balance(new BigDecimal("100.00")).currency("USD").user(senderUser).build();
        Wallet receiverWallet=Wallet.builder().id(2L).balance(new BigDecimal("50.00")).currency("USD").user(receiverUser).build();

        when(walletRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByIdWithLock(2L))
                .thenReturn(Optional.of(receiverWallet));

        TransferRequest request = new TransferRequest();
        request.setSenderWalletId(1L);
        request.setReceiverWalletId(2L);
        request.setAmount(new BigDecimal("150.00"));

        //Act + Assert

        assertThrows(InsufficientFundsException.class,()->transactionService.transferFunds(request));
    }

    @Test
    @DisplayName("Transfer fails when receiver wallet does not exist")
    void transferFunds_receiverWalletNotFound() {
        //Arrange

        when(authentication.getName())
                .thenReturn("sender@example.com");

        User senderUser = User.builder()
                .id(1L)
                .email("sender@example.com")
                .role(Role.USER)
                .build();

        Wallet senderWallet = Wallet.builder()
                .id(1L)
                .balance(new BigDecimal("100.00"))
                .currency("USD")
                .user(senderUser)
                .build();

        when(walletRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByIdWithLock(2L))
                .thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest();
        request.setSenderWalletId(1L);
        request.setReceiverWalletId(2L);
        request.setAmount(new BigDecimal("25.00"));

        //Act+Assert

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService.transferFunds(request)
        );
    }
    @Test
    @DisplayName("Transfer fails when sender attempts IDOR transfer from someone else's wallet")
    void transferFunds_unauthorizedSender() {
        // Arrange
        when(authentication.getName()).thenReturn("attacker@example.com");

        User victimUser = User.builder().id(1L).email("victim@example.com").role(Role.USER).build();
        Wallet senderWallet = Wallet.builder().id(1L).balance(new BigDecimal("100.00")).currency("USD").user(victimUser).build();

        when(walletRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderWallet));

        TransferRequest request = new TransferRequest();
        request.setSenderWalletId(1L);
        request.setReceiverWalletId(2L);
        request.setAmount(new BigDecimal("25.00"));

        //Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> transactionService.transferFunds(request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Transfer fails when sender has insufficient funds and does not save transaction")
    void transferFunds_insufficientFunds_verifiesNoSave() {
        when(authentication.getName()).thenReturn("sender@example.com");

        User senderUser = User.builder().id(1L).email("sender@example.com").role(Role.USER).build();
        User receiverUser = User.builder().id(2L).email("receiver@example.com").role(Role.USER).build();

        Wallet senderWallet = Wallet.builder().id(1L).balance(new BigDecimal("100.00")).currency("USD").user(senderUser).build();
        Wallet receiverWallet = Wallet.builder().id(2L).balance(new BigDecimal("50.00")).currency("USD").user(receiverUser).build();

        when(walletRepository.findByIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByIdWithLock(2L)).thenReturn(Optional.of(receiverWallet));

        TransferRequest request = new TransferRequest();
        request.setSenderWalletId(1L);
        request.setReceiverWalletId(2L);
        request.setAmount(new BigDecimal("150.00"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.transferFunds(request));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

}
