package com.telus.credit.migration.thrd;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Test {

	public static void main(String[] args) {
	    CompletableFuture<String> r1 = CompletableFuture.supplyAsync(() -> {
	        try {
	        	System.out.println("41 called.");
	            Thread.sleep(50);
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        }
	        return "41";
	    });

	    CompletableFuture<String> r2 = CompletableFuture.supplyAsync(() -> {
	        System.out.println("42 called.");
	        return "42";
	    });
	    CompletableFuture<String> r3 = CompletableFuture.supplyAsync(() -> {
	        System.out.println("43 called.");
	        return "43";	    });
	    
	    
	    CompletableFuture.allOf(r1, r2, r3).thenRun(() -> { System.out.println("End."); });
	    
	    Stream.of(r1, r2, r3).forEach(System.out::println);
	}

}
