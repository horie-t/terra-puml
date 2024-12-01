package com.t_horie.tf2puml;

import com.t_horie.tf2puml.adapter.in.cli.TerraPumlRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TerraPumlApplicationTests {

	@MockBean
	private TerraPumlRunner terraPumlRunner;

	@Test
	void contextLoads() throws Exception {
		Mockito.doNothing().when(terraPumlRunner).run(Mockito.any());
	}

}
