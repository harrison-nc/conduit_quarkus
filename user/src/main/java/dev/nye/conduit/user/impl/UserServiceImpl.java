package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.Registration;
import dev.nye.conduit.user.User;
import dev.nye.conduit.user.UserMapper;
import dev.nye.conduit.user.UserService;
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
  public User register(Registration req) {
    var entity = mapper.toEntity(req);
    entityManager.persist(entity);
    return null;
  }
}
