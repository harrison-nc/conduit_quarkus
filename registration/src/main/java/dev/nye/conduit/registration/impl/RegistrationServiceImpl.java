package dev.nye.conduit.registration.impl;

import dev.nye.conduit.registration.*;
import java.util.Objects;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class RegistrationServiceImpl implements RegistrationService {

  @Inject RegistrationMapper mapper;

  @Inject EntityManager entityManager;

  private RegistrationResponse toDomain(
      RegistrationEntity entity, Function<RegistrationEntity, RegistrationResponse> fn) {
    Objects.requireNonNull(entity);
    Objects.requireNonNull(fn);
    return fn.apply(entity);
  }

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  @Override
  public RegistrationResponse register(RegistrationRequest reg) {
    var entity = mapper.toEntity(reg);
    entityManager.persist(entity);
    return mapper.toDomain(reg);
  }
}
