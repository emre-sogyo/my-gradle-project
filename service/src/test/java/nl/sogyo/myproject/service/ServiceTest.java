package nl.sogyo.myproject.service;

import nl.sogyo.myproject.domain.Note;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceTest {

    @Test
    void test(){
        assertNotNull(new Note());
    }
}
