package org.springframework.data.jooq.repository.sample;

import org.springframework.data.jooq.repository.JooqRepository;
import org.springframework.stereotype.Repository;
import sample.tables.pojos.Users;

@Repository
public interface UserRepository extends JooqRepository<Users, Integer> {
}
