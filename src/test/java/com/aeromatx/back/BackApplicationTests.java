package com.aeromatx.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=your_base64_encoded_secret_key_for_this_test",
    "jwt.expiration.ms=3600000"
})

    // ...

class BackApplicationTests {

	@Test
	void contextLoads() {
	}

}
