package com.lisz;

@FunctionalInterface
public interface IHandlerInterface {
	void handle(Request request, Response response);
}
