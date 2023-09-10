package ru.murad.restful.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.murad.restful.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
