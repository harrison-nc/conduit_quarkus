package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

  @Inject UserMapper mapper;

  @Inject EntityManager entityManager;

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  @Override
  public RegistrationResponse register(Registration req) {
    var entity = mapper.toEntity(req);
    entityManager.persist(entity);
    return mapper.toDomain(entity);
  }
}
