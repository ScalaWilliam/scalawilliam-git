# Provisioning

Using [Ansible](https://www.ansible.com/get-started):

```
$ SW_GIT=git.digitalocean.scalawilliam.com
$ ansible all --user=root -i $SW_GIT, -a 'apt -y install python'
$ ansible-playbook --user=root -i $SW_GIT, ansible/prod-playbook.yml
$ curl -i https://$SW_GIT/
```