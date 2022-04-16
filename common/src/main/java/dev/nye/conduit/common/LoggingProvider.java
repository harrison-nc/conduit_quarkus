package dev.nye.conduit.common;

import java.util.stream.Collectors;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class LoggingProvider implements ContainerRequestFilter, ClientRequestFilter {

  private <K, V> void print(String h, MultivaluedMap<K, V> m) {
    System.out.format(
        "%n==== %s ====%n%s",
        h,
        m.entrySet().stream()
            .map(
                e ->
                    "%s => %s%n"
                        .formatted(
                            e.getKey(),
                            e.getValue().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))))
            .collect(Collectors.joining()));
  }

  @Override
  public void filter(ContainerRequestContext ctx) {
    print("Rest", ctx.getHeaders());
  }

  @Override
  public void filter(ClientRequestContext ctx) {
    print("Rest Client", ctx.getHeaders());
  }
}
