/**
 * Header Component
 * @param {String} title for the application title 
 * @returns the Header component
 */
const Header = ({ title }) => {
    return (
        <header className="Header">
            <h1>{title}</h1>
        </header>
    )
}

export default Header