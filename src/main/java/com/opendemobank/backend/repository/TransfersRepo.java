package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransfersRepo extends JpaRepository<Transfer, Long> {

        Transfer findById(long id);

        List<Transfer> findAll();

        List<Transfer> findAllBySessionUserId(long id);

        List<Transfer> findAllByAccountIBAN(String iban);

        List<Transfer> findAllBySessionUserIdOrReceiverUser_IdOrSenderUser_Id(long id, long id1, long id2);

}
