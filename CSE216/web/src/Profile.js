import { Link } from "react-router-dom";

const Profile = ({user}) => {
    console.log(user);
    return (
        <>
            <Link to={"profile/edit"}><br />
            <img src={user.PICURL} alt="user profile" width="100" height="100"></img>
            <br />
            <div>Name: {user.Name}</div>
            <div>Gender: {user.GI}</div>
            <div>SO: {user.SO}</div>
            <div>Email: {user.Email}</div>
            <div>Note: {user.Role}</div></Link>
        </>
    )
}

export default Profile;