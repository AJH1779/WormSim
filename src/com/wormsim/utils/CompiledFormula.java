/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.utils;

import com.wormsim.utils.StringFormula.Formula;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.tools.ToolProvider;

/**
 *
 * @author ah810
 */
public class CompiledFormula {

	public CompiledFormula(String name) {
		this.name = name;
		builder = new StringBuilder("package dynamic.wormsim;"
						+ "import com.wormsim.utils.StringFormula.Formula;"
						+ "public class CompiledFormula").append(name)
						.append(" implements Formula {public double evaluate(){");
	}
	private final String name;
	private final StringBuilder builder;

	public Formula compile()
					throws IOException,
								 ClassNotFoundException,
								 InstantiationException,
								 IllegalAccessException {
		builder.append("}}");
		File root = new File("");
		File temp = new File(root, "dynamic/wormsim/temp_ " + name + ".java");
		Files.write(temp.toPath(), builder.toString().getBytes(
						StandardCharsets.UTF_8));
		ToolProvider.getSystemJavaCompiler().run(null, null, null, temp.getPath());

		URLClassLoader loader = URLClassLoader.newInstance(new URL[]{root.toURI()
			.toURL()});
		Class cls = Class.forName("dynamic.wormsim.CompiledFormula" + name, true,
						loader);
		Formula instance = (Formula) cls.newInstance();
		temp.delete();
		return instance;
	}
}
