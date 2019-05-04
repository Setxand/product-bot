package com.productbot.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("url")
@Getter
@Setter
public class UrlProps {

	private Map<String, String> map = new HashMap<>();

}
