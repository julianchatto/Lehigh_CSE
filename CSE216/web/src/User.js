import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from './api/posts';

const User = () => {
    const { id } = useParams(); // Use useParams to access the commentID and post ID from the URL
    const [user, setUser] = useState({});
     // Fetch the existing comment using the useEffect hook
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get(`/users/${id}`, {
                headers: {
                    'Cache-Control': "private, max-age=604800"
                },
            });
                console.log(response.data.mData);
                setUser(response.data.mData); // Set the fetched comment text to state
            } catch (err) {
                console.error("Failed to fetch user", err);
            }
        };
        fetchUser();
    }, [id]);

    return (
        <>
            <br />
            <img src={user.PICURL} alt="user profile" width="100" height="100"></img>
            <br />
            <div>Name: {user.Name}</div>
            <div>Email: {user.Email} </div>
            <div>Role: {user.Role}</div>
        </>
    )
}

export default User;