package com.wakutabi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TemporaryController {

	@GetMapping("favicon.ico")
	@ResponseBody
	String favicon(){ return ""; }
	
	@GetMapping("{filename}")
	String show(@PathVariable("filename") String filename) {
		return filename;
	}

	@GetMapping("{folder:layouts|travels}/{filename}")
	String show(@PathVariable("folder") String folder, @PathVariable("filename") String filename) {
		return folder + "/" + filename;
	}
}