package com.wakutabi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
@Slf4j
public class TemporaryController {

	@GetMapping("favicon.ico")
	@ResponseBody
	String favicon(){ return ""; }
	
	@GetMapping("{filename}")
	String show(@PathVariable("filename") String filename) {
		log.info("filename = {}", filename);
		return filename;
	}


	@GetMapping("{folder:layouts|users|travels|infos}/{filename}")
	String show(@PathVariable("folder") String folder, @PathVariable("filename") String filename) {
		log.info("filename = {}", filename);
		return folder + "/" + filename;
	}
}