import pytest
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import declarative_base, sessionmaker, relationship, joinedload
from sqlalchemy.sql import insert

Base = declarative_base()

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

@pytest.fixture
def db_session():
    engine = create_engine('sqlite:///:memory:')
    Base.metadata.create_all(engine)
    Session = sessionmaker(bind=engine)
    session = Session()
    yield session
    session.close()

@pytest.fixture
def populated_db():
    engine = create_engine('sqlite:///:memory:')
    Base.metadata.create_all(engine)
    Session = sessionmaker(bind=engine)
    session = Session()
    
    # Populate test data
    users = [User(name=f"User {i}") for i in range(100)]
    session.add_all(users)
    session.commit()
    
    posts = []
    for u in users:
        posts.append(Post(title=f"Post by {u.name}", user_id=u.id))
    session.add_all(posts)
    session.commit()
    
    yield session
    session.close()

# ----------------- Benchmark 1: Insert performance -----------------

def test_sequential_insert_loop(benchmark, db_session):
    def run():
        for i in range(200):
            user = User(name=f"User {i}")
            db_session.add(user)
        db_session.commit()
    benchmark(run)

def test_bulk_insert_batch(benchmark, db_session):
    def run():
        users = [{"name": f"User {i}"} for i in range(200)]
        db_session.execute(insert(User), users)
        db_session.commit()
    benchmark(run)

# ----------------- Benchmark 2: Query performance (N+1 vs Eager) -----------------

def test_n_plus_one_query_loop(benchmark, populated_db):
    def run():
        # First query fetches all posts (1 query)
        posts = populated_db.query(Post).all()
        # Accessing .user for each post triggers a separate lazy loading query (N queries)
        names = [post.user.name for post in posts]
        return names
    benchmark(run)

def test_eager_load_joinedload(benchmark, populated_db):
    def run():
        # Eager load the user relationship using a JOIN in a single database round trip
        posts = populated_db.query(Post).options(joinedload(Post.user)).all()
        names = [post.user.name for post in posts]
        return names
    benchmark(run)
