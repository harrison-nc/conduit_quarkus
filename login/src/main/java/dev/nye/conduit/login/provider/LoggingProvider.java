package dev.nye.conduit.login.provider;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;


@Provider
public class LoggingProvider implements ContainerRequestFilter, ClientRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) {
        System.out.format("%n%s%n", ctx.getHeaders().entrySet().stream()
                .map(e -> "## %s => %s".formatted(e.getKey(), String.join(",", e.getValue())))
                .collect(Collectors.joining("\n")));
    }

    @Override
    public void filter(ClientRequestContext ctx) {
        System.out.format("%n%s%n", ctx.getHeaders().entrySet().stream()
                .map(e -> "# %s => %s".formatted(e.getKey(), e.getValue().stream().map(Object::toString).collect(Collectors.joining(","))))
                .collect(Collectors.joining("\n")));
    }
}
