import time
import pytest
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import declarative_base, sessionmaker, relationship, joinedload
from sqlalchemy.sql import insert

Base = declarative_base()

NUM_USERS = 100
NUM_INSERTS = 200


class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    name = Column(String)


class Post(Base):
    __tablename__ = 'posts'
    id = Column(Integer, primary_key=True)
    title = Column(String)
    user_id = Column(Integer, ForeignKey('users.id'))
    user = relationship("User", back_populates="posts")


User.posts = relationship("Post", order_by=Post.id, back_populates="user")


def _make_session(engine):
    return sessionmaker(bind=engine)()


def _make_engine():
    engine = create_engine('sqlite:///:memory:')
    Base.metadata.create_all(engine)
    return engine


def sequential_insert(session):
    for i in range(NUM_INSERTS):
        session.add(User(name=f"User {i}"))
    session.commit()
    return session.query(User).count()


def bulk_insert(session):
    session.execute(insert(User), [{"name": f"User {i}"} for i in range(NUM_INSERTS)])
    session.commit()
    return session.query(User).count()


@pytest.fixture
def populated_engine():
    engine = create_engine('sqlite:///:memory:')
    Base.metadata.create_all(engine)
    Session = sessionmaker(bind=engine)
    session = Session()
    users = [User(name=f"User {i}") for i in range(NUM_USERS)]
    session.add_all(users)
    session.commit()
    posts = [Post(title=f"Post by {u.name}", user_id=u.id) for u in users]
    session.add_all(posts)
    session.commit()
    session.close()
    return engine


def n_plus_one_query(session):
    posts = session.query(Post).all()
    return [post.user.name for post in posts]


def eager_load_query(session):
    posts = session.query(Post).options(joinedload(Post.user)).all()
    return [post.user.name for post in posts]


def test_bulk_insert_faster_than_sequential():
    """Prove bulk insert (single statement) beats sequential ORM adds."""
    rounds = 3

    sess_seq = _make_session(_make_engine())
    start = time.perf_counter()
    for _ in range(rounds):
        sequential_insert(sess_seq)
    t_seq = time.perf_counter() - start
    sess_seq.close()

    sess_bulk = _make_session(_make_engine())
    start = time.perf_counter()
    for _ in range(rounds):
        bulk_insert(sess_bulk)
    t_bulk = time.perf_counter() - start
    sess_bulk.close()

    assert t_bulk < t_seq, (
        f'Bulk insert ({t_bulk:.6f}s) should be faster than '
        f'sequential ({t_seq:.6f}s)'
    )


def test_eager_load_faster_than_n_plus_one(populated_engine):
    """Prove eager-load (JOIN) beats N+1 lazy-loading."""
    rounds = 3

    session_n1 = _make_session(populated_engine)
    start = time.perf_counter()
    for _ in range(rounds):
        n_plus_one_query(session_n1)
    t_n1 = time.perf_counter() - start
    session_n1.close()

    session_eager = _make_session(populated_engine)
    start = time.perf_counter()
    for _ in range(rounds):
        eager_load_query(session_eager)
    t_eager = time.perf_counter() - start
    session_eager.close()

    assert t_eager < t_n1, (
        f'Eager load ({t_eager:.6f}s) should be faster than '
        f'N+1 queries ({t_n1:.6f}s)'
    )
