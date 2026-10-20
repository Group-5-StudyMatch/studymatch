//Grab the routing tools we need from React Router
// NavLink is for clickable links , Outlet is a placeholder for the actual pages.
import { NavLink, Outlet } from 'react-router-dom';

// set up the main layout shell. This UI stays put while the pages inside it swap out
export default function Layout() {
    return (
        // wrap everything into a container. Limit the width so it doesnt extend forever.
        // create a navigation bar with links to the different pages of the app
        // the actual buttons to click
        <div style={{maxWidth: 900, margin: '0 auto', padding: 16}}>
            
            <nav style={{display: 'flex', gap: 16,marginBottom: 24}}>

                
                <NavLink to="/">Home</NavLink>
                <NavLink to="/students">Students</NavLink>
            </nav>

            <Outlet />

        </div>
        )
    }