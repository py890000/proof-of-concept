package org.springframework.data.jooq.repository.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sample.tables.pojos.Users;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfig.class,SampleTestConfig.class})
@ComponentScan(basePackageClasses = UserRepository.class)
public class SampleRepositoryTest {


    @Autowired
    UserRepository userRepository;

    @Test
    public void findById() throws Exception {
        Users joe = userRepository.findOne(1);

        assertThat("User not found",joe.getId(),is(1));

    }
}