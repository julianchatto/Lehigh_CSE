import Header from './Header';
import Nav from './Nav';
import Home from './Home';
import NewPost from './NewPost';
import PostPage from './PostPage';
import EditComment from './EditComment.js';
import Profile from './Profile.js';
import NewComment from './NewComment.js';
import User from './User.js';
import EditProfile from './EditProfile.js';
import { Route, Switch, useLocation, useHistory } from 'react-router-dom';
import { DataProvider } from './context/DataContext';
import { useEffect, useState } from 'react';
import api from './api/posts';

// const {google} = require('googleapis');



const useScript = (src) => {
  	useEffect(() => {
		const script = document.createElement('script');
		script.src = src;
		script.async = true;
		document.getElementById("signInID").appendChild(script);

		return () => {
			const parentElement = document.getElementById("signInID");
			const script = document.getElementById("script");
			if (script) {
				parentElement.removeChild(script);
			}
		};
	}, [src]);
};

function App() {
	const [user, setUser] = useState({});

	const history = useHistory();

	useScript("https://accounts.google.com/gsi/client");

	async function handleCallbackResponse(response) {
		const result = await api.post(`/oauth?idToken=${response.credential}`);
		setUser(result.data.mData);
		console.log(result.data.mData);
		document.getElementById("signInID").hidden = true;
	}

	function handleSignOut(e) {
		setUser({});
		document.getElementById("signInID").hidden = false;
		history.push('/');
	}

	useEffect(() => {
		if (window.google) {
			window.google.accounts.id.initialize({
				client_id: "88208185187-v8p5a4tctco72apced3n3ji9koemt47g.apps.googleusercontent.com",
				callback: handleCallbackResponse
			});
			window.google.accounts.id.renderButton(
				document.getElementById("signInID"),
				{ theme: "outline", size: "large" }
			);
			window.google.accounts.id.prompt();
		}
	}, [user]);

	return (
		<div className="App">
			 
		<Header title="The Buzz" />
			<DataProvider>

				<div id="signInID"></div>
				{Object.keys(user).length !== 0 && <Nav />}

				{Object.keys(user).length === 0 && <h1>Sign In To View Ideas</h1>}
				{Object.keys(user).length !== 0 && 
					<Switch>
						<Route exact path="/" render={(props) => <Home {...props} user={user} />} />
						<Route exact path="/post" render={(props) => <NewPost {...props} user={user} />}/>
						<Route exact path="/profile" render={(props) => <Profile {...props} user={user}/>} />
						<Route exact path="/post/:id/comments/:commentID" component={EditComment} />
						<Route exact path="/post/:id/comments" component={NewComment} />
						<Route exact path="/user/:id" component={User} />
						<Route exact path="/profile/edit" render={(props) => <EditProfile {...props} user={user}/>} />
						<Route path="/post/:id" render={(props) => <PostPage {...props} user={user}/>} />
					</Switch> 
				}    
			</DataProvider>
			{ useLocation().pathname === "/profile" && Object.keys(user).length !== 0 &&<button className="Footer" onClick={ (e) => handleSignOut(e)}>Sign out</button> }
		</div>
	);
}

export default App;