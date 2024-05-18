import { Link } from 'react-router-dom';

/**
 * The navigation bar for the application
 * @returns the Nav component
 */
const Nav = () => {
    return (
        <nav className="Nav">  
            <ul>
                <li><Link to="/">Home</Link></li>
                <li><Link to="/post">New Idea</Link></li>
                <li><Link to="/profile">Profile</Link></li>
            </ul>
        </nav>
    )
}

export default Nav;