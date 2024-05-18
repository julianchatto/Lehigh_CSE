import Feed from './Feed';
import { useContext } from 'react';
import DataContext from './context/DataContext';

/**
 * The home page of the application
 * Ensures that the psots are loaded before displaying them
 * @returns the Home component
 */
const Home = ({user}) => {
    const {posts, fetchError, isLoading } = useContext(DataContext);

    return (
        <main className='Home'>
            {isLoading && <p className='statusMsg'>Loading posts...</p>}
            {!isLoading && fetchError && <p className='statusMsg' style={{ color: 'red' }}>{fetchError}</p>}
            {!isLoading && !fetchError && (posts.length ? <Feed posts={posts} user={user}/> : <p className='statusMsg'>No posts to display.</p>)}
        </main>
    )
}

export default Home;